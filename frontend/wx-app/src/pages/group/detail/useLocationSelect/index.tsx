import React, { useEffect, useState } from "react";
import request from "@/utils/request";
import { Text, View } from "@tarojs/components";
import styles from "./index.module.less";
import { Icon, Picker } from "@antmjs/vantui";

const useLocationSelect: (props?: {
  extra?: React.ReactNode;
  footer?: React.ReactNode;
  onSelect?: (e: any) => void;
}) => [
  {
    currentArea?: {
      id: string;
      name: string;
    };
    loading: boolean;
  },
  React.ReactNode
] = (props = {}) => {
  const { extra, footer, onSelect } = props;
  const [areaList, setAreaList] = useState<any[]>([]); // 区域列表
  const [locationLoading, setLocationLoading] = useState(false);
  const [addressPickVisible, setAddressPickVisible] = useState(false);
  const [currentArea, setCurrentArea] = useState<{
    id: string;
    name: string;
  }>();

  // 获取区域列表
  const fetchLocationList = async (params: {
    latitude: number;
    longitude: number;
    name?: string;
    page: number;
    size: number;
    enable?: number;
  }) => {
    const res = await request.get("/client/store/area/distance", { params });

    if (res.success) {
      setAreaList(res.data || []);
      setCurrentArea(res?.data?.[0]);
    }
  };
  // 获取当前位置信息
  const getWxLocation = () => {
    setLocationLoading(true);
    wx.getLocation({
      type: "wgs84",
      success(res) {
        wx.setStorageSync("locationInfo", res);
        fetchLocationList({
          page: 1,
          size: 10,
          enable: 1,
          latitude: res?.latitude,
          longitude: res?.longitude,
        }).finally(() => {
          setLocationLoading(false);
        });
      },
      fail(error) {
        setLocationLoading(false);
        console.error("获取位置失败", error);
        wx.showToast({ title: "获取位置失败" });
      },
    });
  };

  useEffect(() => {
    getWxLocation();
  }, []);

  return [
    { currentArea, loading: locationLoading },
    <View className={styles.header}>
      <View className={styles.storeTitle}>
        <View onClick={() => setAddressPickVisible(true)}>
          {currentArea?.name ? (
            <View style={{ display: "inline-flex" }}>
              <Picker
                title="选择地址"
                columns={areaList?.map((it) => ({
                  text: it.name,
                  value: it.id,
                }))}
                idKey={"value"}
                onConfirm={(e) => {
                  const find = areaList.find(
                    (it) => it.id === e?.detail?.value?.value
                  );
                  if (find?.id !== currentArea?.id) {
                    setCurrentArea(find);
                    onSelect?.(find);
                  }

                  setAddressPickVisible(false);
                }}
                mode={"content"}
                allowClear={false}
                onCancel={() => setAddressPickVisible(false)}
                key={currentArea?.id}
                value={currentArea?.id ? [currentArea?.id] : undefined}
              />
              <Text className={"ml-4"}>{">"}</Text>
            </View>
          ) : null}
        </View>
        <View>{extra}</View>
      </View>
      <View className={styles["store-location"]}>
        <Text className={styles.text}>{currentArea?.address || ""}</Text>
        <Icon name={"location-o"} size={12} className={styles.icon} />
      </View>
      {footer}
    </View>,
  ];
};
export default useLocationSelect;
