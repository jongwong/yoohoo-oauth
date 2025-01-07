import React, { useState } from "react";
import { View } from "@tarojs/components";
import styles from "./index.module.less";
import {
  Avatar,
  Image,
  Progress,
  Swiper,
  SwiperItem,
} from "@nutui/nutui-react-taro";
import classNames from "classnames";
import Taro from "@tarojs/taro";
import Layout from "../../component/Layout";

const Index: React.FC = () => {
  const [current, setCurrent] = useState(0);
  const banners = [
    "//yoohoo-oss.oss-cn-shanghai.aliyuncs.com/fontend/home/swiper/1.png",
    "//yoohoo-oss.oss-cn-shanghai.aliyuncs.com/fontend/home/swiper/2.png",
  ];

  return (
    <Layout edge={"none"}>
      <View className={styles.container}>
        {/* 轮播图组件 */}
        <Swiper
          className={styles.banner}
          autoplay
          defaultValue={0}
          onChange={(e) => {
            setCurrent(e.detail.current);
          }}
          height={"40vh"}
          indicatorDots={false}
          duration={20000}
          loop
          indicator={
            <View className={styles.indicatorContainer}>
              {banners.map((_, index) => (
                <View
                  key={index}
                  className={styles.indicatorItem}
                  style={{
                    backgroundColor:
                      index === current ? "#fff" : "rgba(255, 255, 255, 0.5)",
                  }}
                />
              ))}
            </View>
          }
        >
          {banners.map((img, index) => (
            <SwiperItem key={index}>
              <View className={styles.imageContainer}>
                <Image src={img} className={styles.image} mode="aspectFill" />
                {/* 渐变层 */}
                <View className={styles.gradientLayer}></View>
              </View>
            </SwiperItem>
          ))}
        </Swiper>
        <View className={styles.userInfoCard}>
          <View className={styles.userInfo}>
            <Avatar
              className={styles.avatar}
              color="#fff"
              background="#eeeeee"
            />
            <View className={styles.userBox}>
              <View className={styles.userName}>王忠(JongWong)</View>
              <View className={styles.integral}>
                <View className={styles.integralBar}>
                  <Progress
                    strokeWidth="4"
                    percent={30}
                    color="linear-gradient(270deg, rgba(140, 178, 75, 1) 0%, rgba(102, 152, 69, 1) 40%, rgba(44, 155, 75, 1) 100%)"
                  />
                </View>
                <View className={styles.integralText}>73/1000 {" 积分"}</View>
              </View>
            </View>
          </View>

          <View className={styles.coupons}>
            <View>0</View>
            <View>优惠券</View>
          </View>
        </View>

        <View className={styles.pickupBox}>
          {/* 企业入口 */}
          <View
            className={classNames(styles.deliveryItem, styles.enterpriseItem)}
          >
            <View className={styles.deliveryTitle}>企业</View>
            <View className={styles.deliveryDescription}>专享企业定制服务</View>
          </View>
          {/* 单人点餐 */}
          <View
            className={styles.pickupItem}
            onClick={() => {
              Taro.switchTab({
                url: "/pages/classify/index",
              });
            }}
          >
            <View className={styles.pickupTitle}>个人</View>
            <View className={styles.pickupDescription}>快速轻松下单</View>
          </View>
        </View>

        <View className={styles.integralBanner}>
          <Image
            src="//yoohoo-oss.oss-cn-shanghai.aliyuncs.com/fontend/home/banner/integral.jpeg" // 确保图片路径正确
            className={styles.bannerImage}
            mode="aspectFill" // 使用 aspectFill 来确保图片填充容器
          />
          {/*<View className={styles.arrowButton}>*/}
          {/*  <ArrowDown size={10} color="#fff" /> /!* 使用 NutUI 的箭头图标 *!/*/}
          {/*</View>*/}
        </View>

        <View className={styles.integralBanner} style={{ marginTop: 0 }}>
          <Image
            src="//yoohoo-oss.oss-cn-shanghai.aliyuncs.com/fontend/home/banner/recommend.png" // 确保图片路径正确
            className={styles.bannerImage}
            mode="aspectFill" // 使用 aspectFill 来确保图片填充容器
          />
          {/*<View className={styles.arrowButton}>*/}
          {/*  <ArrowDown size={10} color="#fff" /> /!* 使用 NutUI 的箭头图标 *!/*/}
          {/*</View>*/}
        </View>
      </View>
    </Layout>
  );
};

export default Index;
