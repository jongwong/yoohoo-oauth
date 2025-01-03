import React from "react";

import { ConfigProvider } from "@nutui/nutui-react-taro";

import styles from "./index.module.less";

type LayoutProps = {
  children?: React.ReactNode;
  style?: React.CSSProperties;
};
const Layout: React.FC<LayoutProps> = (props) => {
  const { children, style, ...rest } = props;
  const darkTheme = {
    nutuiColorPrimary: "#59bc9a",
    nutuiColorPrimaryStop1: "#59bc9a",
    nutuiColorPrimaryStop2: "#59bc9a",
  };
  return (
    <ConfigProvider
      className={styles["yoohoo-layout"]}
      theme={darkTheme}
      style={style}
    >
      {children}
    </ConfigProvider>
  );
};
export default Layout;
