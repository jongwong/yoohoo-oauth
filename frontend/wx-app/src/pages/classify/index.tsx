import React, { ReactNode, useEffect, useMemo, useRef, useState } from "react";
import { Image, ScrollView, Text, View } from "@tarojs/components";
import { Button, Empty, Icon, Picker, Space, Sticky } from "@antmjs/vantui";

import { useRequest } from "ahooks";
import dayjs, { Dayjs } from "dayjs";
import { groupBy } from "lodash-es";
import classNames from "classnames";
import request from "@/utils/request";
import ProductCardItem from "./component/ProductCardItem";
import { generateFileUrl, getNoDataUrl } from "@/utils/file";
import styles from "./index.module.less";
import Taro from "@tarojs/taro";
import SwiperDatePicker from "./component/DatePicker";
import Layout from "@/component/Layout";

const Index: React.FC = () => {
  // State hooks
  const [activeIndex, setActiveIndex] = useState(0); // 当前激活的菜单项
  const [scrollToId, setScrollToId] = useState<string>(""); // 滚动到的目标
  const [selectTime, setSelectTime] = useState<Dayjs>(dayjs()); // 选择时间
  const [productList, setProductList] = useState<any[]>([]); // 产品列表
  const [currentArea, setCurrentArea] = useState<{
    id: string;
    name: string;
  }>(); // 当前选中的区域
  const [addressPickVisible, setAddressPickVisible] = useState(false); // 地址选择弹窗是否显示
  const [areaList, setAreaList] = useState<any[]>([]); // 区域列表
  const [locationLoading, setLocationLoading] = useState(false);
  const [cartMap, setCartMap] = useState({});

  // Refs
  const isScrollingByClick = useRef(false); // 是否为点击触发滚动
  const scrollTimer = useRef<NodeJS.Timeout | null>(null); // 用于清理滚动状态的定时器

  // 数据请求
  const { data: menuList = [] } = useRequest(async () => {
    const res = await request.get("/client/menus/category");
    return res?.data || [];
  });

  // 请求产品列表数据
  const { loading: productLoading } = useRequest(
    async () => {
      return request.get("/client/group/product", {
        params: {
          page: 1,
          size: 400,
          time_delivery_start: selectTime?.startOf("day").valueOf(),
          time_delivery_end: selectTime?.endOf("day").valueOf(),
          group_status: [20, 30],
          distribution_point_id: currentArea?.id,
        },
      });
    },
    {
      refreshDeps: [currentArea, selectTime],
      ready: !!currentArea?.id && !!selectTime,
      onSuccess: (res) => {
        if (res.success) {
          setProductList(res?.data || []);
        }
      },
    }
  );

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

  useEffect(() => {
    getWxLocation();
  }, []);

  // 格式化菜单数据
  const formatMenuData = useMemo(() => {
    return (menuList || []).sort((a, b) => a.sort - b.sort);
  }, [menuList]);

  // 根据分类获取产品列表
  const productListCategoryMap = useMemo(() => {
    return groupBy(productList, (it) => it?.category_id);
  }, [productList]);

  const getCategoryProductList = (id: string) => {
    return productListCategoryMap[id] || [];
  };

  // 点击菜单触发滚动
  const handleMenuClick = (index: number) => {
    if (scrollTimer.current) {
      clearTimeout(scrollTimer.current);
      scrollTimer.current = null;
    }

    isScrollingByClick.current = true;
    setActiveIndex(index);
    setScrollToId(`content-${menuList[index].id}`);

    scrollTimer.current = setTimeout(() => {
      isScrollingByClick.current = false;
    }, 500);
  };

  // 手动滚动触发
  const handleContentScroll = (e) => {
    if (isScrollingByClick.current) return;

    const query = Taro.createSelectorQuery();
    query
      .selectAll(".content-title")
      .boundingClientRect((rects: any[]) => {
        for (let i = 0; i < rects?.length; i++) {
          const rect = rects[i];
          if (rect.top >= 0) {
            setActiveIndex(i);
            break;
          }
        }
      })
      .exec();
  };

  const productCount = useMemo(() => {
    return Object.keys(cartMap || {})
      .map((it) => cartMap[it])
      .reduce((it, pre) => pre + (it || 0), 0);
  }, [cartMap]);

  return (
    <Layout
      loading={productLoading || locationLoading}
      backgroundColor={"#fff"}
      edge={"none"}
    >
      <View className={styles.container}>
        {/* Header: Store Name and Location */}
        <View className={styles.header}>
          <View className={styles["store-title"]}>
            {/*<Star size={14} className={styles.icon} />*/}
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
                        (it) => it.id === e?.[0]?.value
                      );
                      setCurrentArea(find);
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
          </View>
          <View className={styles["store-location"]}>
            <Text className={styles.text}>{currentArea?.address || ""}</Text>
            <Icon name={"location-o"} size={12} className={styles.icon} />
          </View>
        </View>

        {/* Time Picker */}
        <View className={styles["timePickerBox"]}>
          <SwiperDatePicker value={selectTime} onChange={setSelectTime} />
        </View>

        {/* Menu Container */}
        <View className={styles.menuContainer}>
          <ScrollView className={styles.menu} scrollY>
            {formatMenuData.map((item, index) => (
              <View
                key={item.id}
                className={classNames(styles.menuItem, {
                  [styles.active]: activeIndex === index,
                })}
                onClick={() => handleMenuClick(index)}
              >
                {item.title}
              </View>
            ))}
          </ScrollView>

          {/* Content Section */}
          <ScrollView
            className={styles.content}
            scrollY
            scrollWithAnimation
            scrollIntoView={scrollToId}
            onScroll={handleContentScroll}
          >
            {productList?.length ? (
              formatMenuData.map((item) => {
                const list = getCategoryProductList(item?.category_id);
                const isSpecial = item.title === "团购须知";
                let content: ReactNode = null;

                if (isSpecial && currentArea?.delivery_time_note) {
                  content = <View>{currentArea?.delivery_time_note}</View>;
                }
                if (!content && !list?.length) {
                  return null;
                }

                if (list?.length) {
                  content = (
                    <>
                      {list.map((productIt) => (
                        <ProductCardItem
                          key={productIt.id}
                          title={productIt?.product_name}
                          price={productIt?.price}
                          src={generateFileUrl(
                            productIt?.thumbnail_image_url,
                            true
                          )}
                          onGotoOrderSubmit={() => {
                            Taro.navigateTo({
                              url: `/pages/order/create/index?product_id=${productIt?.id}&area_id=${currentArea?.id}`,
                            });
                          }}
                          data={productIt}
                          onCartChange={(num) => {
                            setCartMap((old) => ({
                              ...old,
                              [productIt.id]: num,
                            }));
                          }}
                          originalPrice={productIt?.original_price}
                        />
                      ))}
                    </>
                  );
                }

                return (
                  <View
                    key={item.id}
                    id={`content-${item.id}`}
                    className={styles.contentBlock}
                  >
                    <View
                      className={classNames(
                        styles.contentTitle,
                        "content-title"
                      )}
                    >
                      {item.title}
                    </View>
                    <View className={styles.contentDescription}>
                      <Space direction="vertical" className={"w-1-1"}>
                        {content}
                      </Space>
                    </View>
                  </View>
                );
              })
            ) : (
              <View
                style={{
                  marginTop: "15vh",
                  height: "100vw",
                  bottom: 0,
                }}
              >
                <Empty
                  description={"暂无商品"}
                  style={{ width: "100vw", flex: "0 0 auto" }}
                  image={<Image src={getNoDataUrl()} />}
                />
              </View>
            )}
          </ScrollView>
        </View>
        {productCount ? (
          <Sticky offsetTop={120}>
            <View className={styles.cartBar}>
              <Button type="primary">去结算</Button>
            </View>
          </Sticky>
        ) : null}
      </View>
    </Layout>
  );
};

export default Index;
