package com.ecommerce.microcommerce.web.util.DTO;

import com.ecommerce.microcommerce.web.model.Product;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ProductMarge extends Product {
    private int marge;
}
