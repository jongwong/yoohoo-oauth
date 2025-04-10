import React, { useRef, useState } from "react";
import Layout from "@/component/Layout";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import useLocationSelect from "@/pages/group/detail/useLocationSelect";
import { View } from "@tarojs/components";

import styles from "./index.module.less";
import dayjs, { Dayjs } from "dayjs";
import SwiperDatePicker from "./component/DatePicker";
import ScrollPage from "@/component/ScrollPage";
import GroupItemCard from "@/pages/admin/group/list/GroupItemCard";
import Taro from "@tarojs/taro";

const Index: React.FC = () => {
  // State hooks
  const [selectTime, setSelectTime] = useState<Dayjs>(dayjs()); // 选择时间

  const actionRef = useRef();
  const [{ currentArea, loading: locationLoading }, LocationSelectHolder] =
    useLocationSelect();

  // 请求产品列表数据
  const { data: groupData, runAsync: fetchData } = useRequest(
    async (params) => {
      return request.get("/client/group", {
        params: {
          ...params,
          time_delivery_start: selectTime?.startOf("day").valueOf(),
          time_delivery_end: selectTime?.endOf("day").valueOf(),
          group_status: [20, 30],
          distribution_point_id: currentArea?.id,
        },
      });
    },
    {
      manual: true,
    }
  );

  return (
    <Layout backgroundColor={"#fff"} edge={"none"} loading={locationLoading}>
      <View className={styles.container}>
        {/* Header: Store Name and Location */}

        {LocationSelectHolder}
        {/* Time Picker */}
        <View className={styles["timePickerBox"]}>
          <SwiperDatePicker value={selectTime} onChange={setSelectTime} />
        </View>

        <ScrollPage
          actionRef={actionRef}
          ready={!!currentArea?.id && !!selectTime}
          request={async (params) => {
            return await fetchData(params);
          }}
        >
          {(data) => (
            <View style={{ padding: "0 10px" }}>
              {data.map((item) => (
                <GroupItemCard
                  key={item.id}
                  item={item}
                  onClick={() => {
                    Taro.navigateTo({
                      url: `/pages/group/detail/index?id=${item.id}`,
                    });
                  }}
                />
              ))}
            </View>
          )}
        </ScrollPage>
      </View>
    </Layout>
  );
};

export default Index;
