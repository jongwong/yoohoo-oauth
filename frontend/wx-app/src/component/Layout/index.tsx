import React from "react";

import { ConfigProvider } from "@antmjs/vantui";

import classNames from "classnames";

import styles from "./index.module.less";

type LayoutProps = {
  children?: React.ReactNode;
  style?: React.CSSProperties;
  edge?: "none";
};
const Layout: React.FC<LayoutProps> = (props) => {
  const { children, edge, style, ...rest } = props;

  return (
    <ConfigProvider
      className={classNames(
        styles["yoohoo-layout"],
        edge && styles["yoohoo-layout-edge-" + edge]
      )}
      style={style}
    >
      {children}
    </ConfigProvider>
  );
};
export default Layout;
