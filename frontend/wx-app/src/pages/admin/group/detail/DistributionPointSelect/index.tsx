import React from "react";
import request from "@/utils/request";
import SearchSelect, { SearchSelectProps } from "@/component/SearchSelect";
import { View } from "@tarojs/components";

type DistributionPointSelectProps = Omit<SearchSelectProps, "request">;
const DistributionPointSelect: React.FC<DistributionPointSelectProps> = (
  props
) => {
  const { ...rest } = props;
  return (
    <SearchSelect
      request={getProductPage}
      fieldNames={{
        label: "name",
        value: "id",
      }}
      placeholder={"请输入地址"}
      optionRender={(e) => {
        return <View>{e?.name}</View>;
      }}
      loadInitialOptions={async (e) => {
        const res = await getProductBatchAll({ ids: e as string[] });

        return res?.data || [];
      }}
      {...rest}
    />
  );
};
export default DistributionPointSelect;

export const getProductPage = (params: Record<string, any>) => {
  return request.get("/client/store/area/distribution-point", { params });
};
export const getProductBatchAll = (data: { keys: string[] }) => {
  return request.post("/client/store/area/distribution-point/batch", data);
};
