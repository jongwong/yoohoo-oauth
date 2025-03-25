import React from "react";
import request from "@/utils/request";
import SearchSelect, { SearchSelectProps } from "@/component/SearchSelect";
import { View } from "@tarojs/components";
import { Image } from "@antmjs/vantui";
import { generateFileUrl } from "@/utils/file";

type ProductSelectProps = Omit<SearchSelectProps, "request">;
const ProductSelect: React.FC<ProductSelectProps> = (props) => {
  const { ...rest } = props;
  return (
    <SearchSelect
      {...rest}
      request={getProductPage}
      fieldNames={{
        label: "name",
        value: "id",
      }}
      placeholder={"请输入商品名称"}
      loadInitialOptions={async (e) => {
        const res = await getProductBatchAll({ ids: e as string[] });

        return res?.data || [];
      }}
      optionRender={(e: any) => {
        const _url = e?.thumbnail_image?.url as string;
        const _fullUrl = generateFileUrl(_url);
        return (
          <View style={{ display: "flex" }}>
            <View style={{ marginRight: "16px" }}>
              <Image src={_fullUrl} width={40} height={40} />
            </View>
            <View>{e.name}</View>
          </View>
        );
      }}
    />
  );
};
export default ProductSelect;

export const getProductPage = (params: Record<string, any>) => {
  return request.get("/client/product", { params });
};
export const getProductBatchAll = (data: { keys: string[] }) => {
  return request.post("/client/product/batch", data);
};
