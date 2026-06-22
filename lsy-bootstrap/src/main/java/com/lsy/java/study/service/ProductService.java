package com.lsy.java.study.service;

import com.example.entity.Product;
import com.example.mapper.ProductMapper;
import org.dromara.easyes.core.conditions.LambdaEsQueryWrapper;
import org.dromara.easyes.core.conditions.update.LambdaEsUpdateWrapper;
import org.dromara.easyes.core.toolkit.EsWrappers;
import org.dromara.easyes.core.toolkit.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    /**
     * 1. 基础多条件过滤
     * 对应 SQL: 查找北京地区、评分4.5以上、且包含“堂食”标签的本地生活餐饮
     */
    public List<Product> searchDiningInBeijing() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                // 标题匹配 "餐饮"
                .match(Product::getTitle, "餐饮")
                // 精确过滤
                .term(Product::getBusinessType, "本地生活")
                .term(Product::getCity, "北京")
                .term(Product::getTags, "堂食") // tags是keyword类型，用term
                // 评分范围
                .range(Product::getRating, 4.5f, null); // gte=4.5, lte=null

        return productMapper.selectList(wrapper);
    }

    /**
     * 2. 电商价格区间与品牌聚合
     * 对应 SQL: 查询“手机数码”下各品牌的平均价格和最高销量
     * 注意: Easy-Es 的聚合API通常返回 Map 或 自定义 DTO，这里简化演示返回列表。
     */
    public void aggregateBrandStats() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                .term(Product::getCategory, "手机数码")
                .range(Product::getPrice, 1000, 8000)
                // 添加聚合：按品牌分组
                .aggregation(FieldUtils.getFieldName(Product::getBrand), AggregationType.TERMS, "brand_stats")
                // 子聚合：平均价格
                .aggregationSub("brand_stats", FieldUtils.getFieldName(Product::getPrice), AggregationType.AVG, "avg_price")
                // 子聚合：最大销量
                .aggregationSub("brand_stats", FieldUtils.getFieldName(Product::getSales), AggregationType.MAX, "max_sales");

        // 执行查询 (通常返回聚合结果集，此处仅演示构建过程)
        // List<Product> list = productMapper.selectList(wrapper); 
        // 实际获取聚合结果需调用 productMapper.selectPage 或自定义 ResultMap
    }

    /**
     * 3. 本地生活 LBS 附近搜索
     * 对应 SQL: 查找距离指定坐标 5km 内的门店，按距离排序
     */
    public List<Product> searchNearbyShops() {
        // 北京某坐标
        double lat = 39.9042;
        double lon = 116.4074;

        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                // 地理距离过滤：5km内
                .geoDistance(Product::getLocation, lat, lon, "5km")
                // 按距离升序排序
                .orderByAscDistance(Product::getLocation, lat, lon);

        return productMapper.selectList(wrapper);
    }

    /**
     * 4. 高亮搜索与字段权重提升
     * 对应 SQL: 搜索“星巴克”，标题匹配权重高于标签
     */
    public List<Product> searchWithHighlight() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                // 多字段匹配，设置权重：title^3, brand^2
                .multiMatch(0.5f, "星巴克 免配送费", 
                        Product::getTitle, 3.0f, 
                        Product::getBrand, 2.0f,
                        Product::getTags, 1.0f)
                // 开启高亮，指定字段
                .highlight(Product::getTitle)
                .highlight(Product::getTags);

        return productMapper.selectList(wrapper);
    }

    /**
     * 5. 前缀搜索 (自动补全)
     * 对应 SQL: 用户输入“美”时的即时联想
     */
    public List<Product> searchWithPrefix() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                // 前缀查询 (注意：通常建议对 .keyword 进行前缀查询，此处假设title已配置)
                .prefix(Product::getTitle, "美")
                // 限制返回字段和数量
                .select(Product::getTitle, Product::getCategory)
                .last("LIMIT 5"); // Easy-Es 中可用 last 拼接，或使用分页

        return productMapper.selectList(wrapper);
    }

    /**
     * 6. 综合排序 (混合排序)
     * 对应 SQL: 文本相关性 70% + 销量热度 30%
     */
    public List<Product> searchWithFunctionScore() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                .match(Product::getTitle, "家电")
                // 函数评分：基于销量的对数计算
                .functionScore(FunctionScoreBuilder.of()
                        .fieldValueFactor(Product::getSales, 0.0001f, FieldValueFactorModifier.LOG1P)
                        .boostMode(CombineFunction.SUM))
                .match(Product::getTitle, "家电"); // 重新添加主查询，避免被functionScore覆盖

        // 注意：Easy-Es 对 function_score 的支持可能需要较新版本，若报错可使用 rawQuery 执行原生JSON
        return productMapper.selectList(wrapper);
    }

    /**
     * 7. 按城市与业务类型多维统计
     * 对应 SQL: Dashboard 报表统计
     */
    public void aggregateCityAndBiz() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                .aggregation(FieldUtils.getFieldName(Product::getCity), AggregationType.TERMS, "city_bucket")
                .aggregationSub("city_bucket", FieldUtils.getFieldName(Product::getBusinessType), AggregationType.TERMS, "biz_type_bucket")
                .aggregationSub("biz_type_bucket", FieldUtils.getFieldName(Product::getSales), AggregationType.SUM, "total_sales");

        // productMapper.selectList(wrapper);
    }

    /**
     * 8. 过滤掉低分商品并分页
     * 对应 SQL: 获取评分>=4.0的商品，每页10条，取第2页
     */
    public List<Product> searchWithPagination() {
        // 分页参数：当前页=2，每页大小=10
        int pageNum = 2;
        int pageSize = 10;

        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                .range(Product::getRating, 4.0f, null)
                // 按创建时间倒序
                .orderByDesc(Product::getCreatedTime)
                // 分页
                .start((pageNum - 1) * pageSize)
                .size(pageSize);

        return productMapper.selectList(wrapper);
    }

    /**
     * 9. 存在性检查 (异常数据处理)
     * 对应 SQL: 查找没有设置经纬度但属于本地生活的异常数据
     */
    public List<Product> searchMissingLocation() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                .term(Product::getBusinessType, "本地生活")
                // 字段不存在
                .notExists(Product::getLocation);

        return productMapper.selectList(wrapper);
    }

    /**
     * 10. 多条件 Should 提升 (猜你喜欢逻辑)
     * 对应 SQL: 必须匹配分类，但如果有“好评如潮”或“包邮”则加分
     */
    public List<Product> searchForRecommend() {
        LambdaEsQueryWrapper<Product> wrapper = EsWrappers.lambdaQuery(Product.class)
                // 必须匹配分类
                .term(Product::getCategory, "服饰鞋包")
                // Should 条件 (加分项)
                .should(term -> term.term(Product::getTags, "好评如潮"))
                .should(term -> term.term(Product::getTags, "包邮"))
                // 至少匹配0个 should 条件 (因为 must 已经保证了基础结果)
                .minimumShouldMatch(0);

        return productMapper.selectList(wrapper);
    }
}