package com.xzh.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.SimpleColumnWidthStyleStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xzh.bean.CmDynamicExcelConfig;
import com.xzh.model.ExcelColumnMeta;
import com.xzh.service.CmDictService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelExportUtils {
    private final ObjectMapper objectMapper;

    @Resource
    private CmDictService dictTranslationService;

    public <T> void export(HttpServletResponse response, CmDynamicExcelConfig config, List<T> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            this.doExportMap(response,config,Collections.emptyList());
            return;
        }
        // 判断传入的是否已经是 Map，如果是，直接强转；如果不是，用 Hutool 转成 Map
        List<Map<String, Object>> mapList;
        if (dataList.get(0) instanceof Map) {
            mapList = (List<Map<String, Object>>) dataList;
        } else {
            mapList = dataList.stream()
                    .map(bean -> BeanUtil.beanToMap(bean, false, true))
                    .collect(Collectors.toList());
        }
        this.doExportMap(response,config,mapList);
    }

    private <T> void doExportMap(HttpServletResponse response, CmDynamicExcelConfig config, List<Map<String, Object>> dataList) {
        try {
            // 1. 解析 JSON 并排序
            List<ExcelColumnMeta> columns = objectMapper.readValue(
                    config.getColumnConfig(),
                    new TypeReference<List<ExcelColumnMeta>>() {}
            );
            Collections.sort(columns);

            // 2. 构建动态表头
            List<List<String>> headList = new ArrayList<>();
            for (ExcelColumnMeta meta : columns) {
                headList.add(Collections.singletonList(meta.getTitle()));
            }

            // 3. 构建动态数据并进行格式化
            List<List<Object>> dataRecords = new ArrayList<>();
            for (Map<String, Object> rowData : dataList) {
                List<Object> row = new ArrayList<>();
                for (ExcelColumnMeta meta : columns) {
                    Object rawValue = rowData.get(meta.getField());
                    row.add(processValue(rawValue, meta));
                }
                dataRecords.add(row);
            }

            // 4. 响应前端文件下载流
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode(config.getFileName(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

            // 5. EasyExcel 执行写入
            EasyExcel.write(response.getOutputStream())
                    .head(headList)
                    // 默认给个基础列宽 15，避免太挤
                    .registerWriteHandler(new SimpleColumnWidthStyleStrategy(15))
                    .sheet(config.getSheetName())
                    .doWrite(dataRecords);
        } catch (Exception e) {
            log.error("动态导出Excel失败, exportCode: {}", config.getExportCode(), e);
            throw new RuntimeException("动态导出Excel失败: " + e.getMessage());
        }
    }

    private Object processValue(Object value, ExcelColumnMeta meta) {
        if (value == null) {
            return null;
        }

        // 1. 字典翻译优先 (如果配置了 dictType，且字典服务已注入)
        if (StringUtils.hasText(meta.getClassCode())) {
            String translated = dictTranslationService.translate(meta.getClassCode(), value);
            // 如果翻译成功就返回翻译后的文本，否则原样输出
            return StringUtils.hasText(translated) ? translated : value;
        }

        // 2. 日期格式化处理 (如果配置了 dataFormat)
        if (StringUtils.hasText(meta.getDataFormat())) {
            if (value instanceof java.util.Date) {
                return DateUtil.format((java.util.Date) value, meta.getDataFormat());
            } else if (value instanceof LocalDateTime) {
                return LocalDateTimeUtil.format((LocalDateTime) value, meta.getDataFormat());
            }
            // 如果是 BigDecimal 或者 Number，可以用 DecimalFormat 格式化 (视需求扩展)
        }

        // 3. 无需处理，原样返回
        return value;
    }
}
