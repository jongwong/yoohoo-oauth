import React, { useRef, useState } from "react";
import { ScrollView, View } from "@tarojs/components";
import styles from "./index.module.less";
import ProductCardItem from "./component/ProductCardItem";
import Taro from "@tarojs/taro";
import classNames from "classnames";
import { Space } from "@nutui/nutui-react-taro";
import { Location, Star } from "@nutui/icons-react-taro";

const state = {
  src: "//img10.360buyimg.com/n2/s240x240_jfs/t1/210890/22/4728/163829/6163a590Eb7c6f4b5/6390526d49791cb9.jpg!q70.jpg",
  title: "伯牙绝弦（茉莉雪芽）",
  originalPrice: 17,
  price: 13,
  shopDescription: "早餐",
};
const Index: React.FC = () => {
  const menu = [
    { title: "营养早餐", id: "breakfast" },
    { title: "阳光午餐", id: "lunch" },
    { title: "夜宴盛味", id: "dinner" },
    { title: "轻茶慢享", id: "afternoon" },
    { title: "囤享时光", id: "takeaway" },
    { title: "取餐须知", id: "notice" },
  ];

  const [activeIndex, setActiveIndex] = useState(0); // 当前激活的菜单项
  const [scrollToId, setScrollToId] = useState<string>(""); // 滚动到的目标
  const isScrollingByClick = useRef(false); // 是否为点击触发滚动
  const scrollTimer = useRef<NodeJS.Timeout | null>(null); // 定时器用于清理滚动状态

  // 点击菜单触发滚动
  const handleMenuClick = (index: number) => {
    // 清除上一次滚动的定时器
    if (scrollTimer.current) {
      clearTimeout(scrollTimer.current);
      scrollTimer.current = null;
    }

    isScrollingByClick.current = true; // 标记为点击滚动
    setActiveIndex(index); // 设置当前激活菜单
    setScrollToId(`content-${menu[index].id}`); // 设置滚动目标

    // 定时器：解除滚动锁定状态
    scrollTimer.current = setTimeout(() => {
      isScrollingByClick.current = false;
    }, 500); // 根据滚动动画时长调整
  };

  // 手动滚动触发
  const handleContentScroll = (e) => {
    if (isScrollingByClick.current) return;

    const query = Taro.createSelectorQuery();

    query
      .selectAll(".content-title") // 为内容区域设置统一的类名
      .boundingClientRect((rects: any[]) => {
        // rects 是所有内容区域的位置信息数组
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
  return (
    <View className={styles.container}>
      <View className={styles.header}>
        {/* 第一行：收藏图标 + 红星商务大厦 */}
        <View className={styles["store-title"]}>
          <Star size={14} className={styles.icon} />
          <View>红星商务大厦{" >"}</View>
        </View>

        {/* 第二行：定位图标 + 具体地址 */}
        <View className={styles["store-location"]}>
          <Location size={12} className={styles.icon} />
          <View className={styles.text}>福建省福州市台江区万达广场</View>
        </View>
      </View>
      <View className={styles.menuContainer}>
        {/* 左侧菜单 */}
        <ScrollView className={styles.menu} scrollY>
          {menu.map((item, index) => (
            <View
              key={item.id}
              className={`${styles.menuItem} ${
                activeIndex === index ? styles.active : ""
              }`}
              onClick={() => handleMenuClick(index)}
            >
              {item.title}
            </View>
          ))}
        </ScrollView>

        {/* 右侧内容 */}
        <ScrollView
          className={styles.content}
          scrollY
          scrollWithAnimation
          scrollIntoView={scrollToId} // 指定滚动目标
          onScroll={handleContentScroll}
        >
          {menu.map((item) => (
            <View
              key={item.id}
              id={`content-${item.id}`} // 绑定对应的内容区域
              className={styles.contentBlock}
            >
              <View
                className={classNames(styles.contentTitle, "content-title")}
              >
                {item.title}
              </View>
              <View className={styles.contentDescription}>
                <Space direction={"vertical"}>
                  <ProductCardItem {...state} />
                  <ProductCardItem {...state} />
                  <ProductCardItem {...state} />
                </Space>
              </View>
            </View>
          ))}
        </ScrollView>
      </View>
    </View>
  );
};

export default Index;
