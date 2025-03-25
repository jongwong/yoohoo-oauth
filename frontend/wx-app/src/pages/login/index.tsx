import React from "react";

import useCheckLogin from "../../hooks/useCheckLogin";

import Layout from "../../component/Layout";

const Index: React.FC = () => {
  // // 在页面加载时隐藏 tabBar
  // Taro.hideTabBar();
  const { gotToRegisteredElement } = useCheckLogin();

  return (
    <Layout edge={"none"}>
      {gotToRegisteredElement ? gotToRegisteredElement : null}
    </Layout>
  );
};

export default Index;
