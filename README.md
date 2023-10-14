# SimmongsBackend

## 🖥️ 프로젝트 소개

<br>
<br>

## 📌 구현 기능

### [Product]

#### 재고 등록

`Post /products/registration`

    {
        "product_code" : " ",
        "product_name" : " ",
        "product_amount" :  ,
        "product_unit" : " ",
        "product_type" : " ",
        "product_creation_date" : " "
    }

<br>

#### 재고 전체 조회

`GET /products/showAll`

<br>

#### 재고 조건 검색

`GET /products/search?product_code=[ ]&product_name=[ ]&product_unit=[ ]&product_type=[ ]&product_start_date=[ ]&product_end_date=[ ]`

|Key|Value|Description|
|---|---|---|
|product_code|||
|product_name|||
|product_unit|||
|product_type|||
|product_start_date|||
|product_end_date|||

<br>
<br>

### [BOM]

<br>
