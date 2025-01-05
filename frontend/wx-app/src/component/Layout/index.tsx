import React from "react";

import { ConfigProvider } from "@nutui/nutui-react-taro";

import styles from "./index.module.less";
import classNames from "classnames";

type LayoutProps = {
  children?: React.ReactNode;
  style?: React.CSSProperties;
  edge?: "none";
};
const Layout: React.FC<LayoutProps> = (props) => {
  const { children, edge, style, ...rest } = props;
  const darkTheme = {
    nutuiColorPrimary: "#8cb24b",
    nutuiColorPrimaryStop1: "#8cb24b",
    nutuiColorPrimaryStop2: "#8cb24b",
  };
  return (
    <ConfigProvider
      className={classNames(
        styles["yoohoo-layout"],
        edge && styles["yoohoo-layout-edge-" + edge]
      )}
      theme={darkTheme}
      style={style}
    >
      {children}
    </ConfigProvider>
  );
};
export default Layout;
