import React from "react";
import { View } from "@tarojs/components";

import UseCheckLogin from "../../hooks/useCheckLogin";

import Layout from "../../component/Layout";

import styles from "./index.module.less";

const Index: React.FC = () => {
  // // 在页面加载时隐藏 tabBar
  // Taro.hideTabBar();
  const { gotToRegisteredElement } = UseCheckLogin();

  return (
    <Layout edge={"none"}>
      <View className={styles.container}>
        {gotToRegisteredElement ? gotToRegisteredElement : null}
      </View>
    </Layout>
  );
};

export default Index;
