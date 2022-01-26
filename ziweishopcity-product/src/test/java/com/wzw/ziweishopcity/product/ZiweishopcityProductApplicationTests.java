package com.wzw.ziweishopcity.product;


import com.wzw.ziweishopcity.product.dao.AttrGroupDao;
import com.wzw.ziweishopcity.product.dao.SkuSaleAttrValueDao;
import com.wzw.ziweishopcity.product.service.CategoryService;
import com.wzw.ziweishopcity.product.service.SkuSaleAttrValueService;
import com.wzw.ziweishopcity.product.vo.SkuItemSaleAttrVo;
import com.wzw.ziweishopcity.product.vo.SpuBaseAttrVo;
import com.wzw.ziweishopcity.product.vo.SpuItemAttrGroupVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class ZiweishopcityProductApplicationTests {

    @Autowired
    CategoryService categoryService;
    @Autowired
    AttrGroupDao attrGroupDao;
    @Autowired
    SkuSaleAttrValueDao skuSaleAttrValueDao;
    @Test
    void contextLoads() {
        List<SkuItemSaleAttrVo> groupAttrBySpuIdAndCatalogId = skuSaleAttrValueDao.getSkuItemSaleAttrVos(4l);
        for (SkuItemSaleAttrVo skuItemSaleAttrVo : groupAttrBySpuIdAndCatalogId) {
            System.out.println(skuItemSaleAttrVo.getAttrId());
            System.out.println(skuItemSaleAttrVo.getAttrName());
            System.out.println(skuItemSaleAttrVo.getAttrValues());
        }
    }
}
