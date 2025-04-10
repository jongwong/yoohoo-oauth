import React, { ReactNode, useMemo, useRef, useState } from "react";
import Layout from "@/component/Layout";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import Taro from "@tarojs/taro";
import ProductCardItem from "@/pages/group/detail/ProductCardItem";
import { generateFileUrl, getNoDataUrl } from "@/utils/file";
import { getFinallyPrice } from "@/utils/product";
import SkuPopup from "@/pages/group/detail/SkuPopup";
import CartPopup from "@/pages/group/detail/CartPopup";
import useLocationSelect from "@/pages/group/detail/useLocationSelect";
import { ScrollView, View } from "@tarojs/components";

import styles from "./index.module.less";
import dayjs, { Dayjs } from "dayjs";
import { groupBy } from "lodash-es";
import SwiperDatePicker from "@/pages/classify/component/DatePicker";
import classNames from "classnames";
import { Empty, Space } from "@antmjs/vantui";

const Index: React.FC = () => {
  // State hooks
  const [activeIndex, setActiveIndex] = useState(0); // 当前激活的菜单项
  const [scrollToId, setScrollToId] = useState<string>(""); // 滚动到的目标
  const [selectTime, setSelectTime] = useState<Dayjs>(dayjs()); // 选择时间

  const [productList, setProductList] = useState<any[]>([]); // 产品列表

  const [_skuCountList, setSkuCountList] = useState<
    {
      data: any;
      product_id: string;
      sku_id?: string;
      count: number;
    }[]
  >([]);
  const isScrollingByClick = useRef(false); // 是否为点击触发滚动
  const scrollTimer = useRef<NodeJS.Timeout | null>(null); // 用于清理滚动状态的定时器

  const skuCountList = _skuCountList.filter((it) => it.count > 0);
  const [{ currentArea }, LocationSelectHolder] = useLocationSelect();

  const { data: menuList = [] } = useRequest(async () => {
    const res = await request.get("/client/menus/category");
    return res?.data || [];
  });

  const [currentGroupId, setCurrentGroupId] = useState("");
  const { loading: deliveryFeeLoading, data: deliveryFee } = useRequest(
    async () => {
      return request.get(`/client/delivery/fee`, {
        params: {
          point_id: currentArea?.id,
        },
      });
    },
    {
      refreshDeps: [currentArea?.id],
      ready: !!currentArea?.id,
    }
  );

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

  const [popupOpenProductId, setPopupOpenProductId] = useState("");
  return (
    <Layout backgroundColor={"#fff"} edge={"none"} loading={productLoading}>
      <View className={styles.container}>
        {/* Header: Store Name and Location */}

        {LocationSelectHolder}
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
                      {list.map((it) => (
                        <ProductCardItem
                          title={it.product_name}
                          src={generateFileUrl(it.thumbnail_image)}
                          originalPrice={it?.market_price}
                          price={getFinallyPrice(it)}
                          productData={it}
                          onOpenSku={() => {
                            setCurrentGroupId(it?.purchase_group_id);
                            setPopupOpenProductId(it.product_id);
                          }}
                          skuCountList={skuCountList}
                          onChange={(e) => {
                            setSkuCountList(e);
                          }}
                          hasMultipleSku={it?.has_multiple_sku}
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
                  image={getNoDataUrl()}
                />
              </View>
            )}
          </ScrollView>
        </View>
      </View>

      <CartPopup
        skuCountList={skuCountList}
        onChange={(e) => {
          setSkuCountList(e);
        }}
        currentAreaId={currentArea?.id}
        deliveryFee={deliveryFee}
      />
      {currentGroupId && popupOpenProductId ? (
        <SkuPopup
          show={!!popupOpenProductId}
          productId={popupOpenProductId}
          onClose={() => {
            setPopupOpenProductId("");
            setCurrentGroupId("");
          }}
          groupId={currentGroupId}
          skuCountList={skuCountList}
          onChange={(e) => {
            setSkuCountList(e);
          }}
        />
      ) : null}
    </Layout>
  );
};

export default Index;
