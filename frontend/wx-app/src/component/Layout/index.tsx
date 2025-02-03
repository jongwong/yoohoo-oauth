import React from "react";

import { ConfigProvider, Loading, Overlay } from "@antmjs/vantui";

import classNames from "classnames";

import styles from "./index.module.less";
import { View } from "@tarojs/components";

type LayoutProps = {
  children?: React.ReactNode;
  style?: React.CSSProperties;
  edge?: "none";
  loading?: boolean;
};
const Layout: React.FC<LayoutProps> = (props) => {
  const { children, loading, edge, style, ...rest } = props;

  return (
    <ConfigProvider
      className={classNames(
        styles["yoohoo-layout"],
        edge && styles["yoohoo-layout-edge-" + edge]
      )}
      style={style}
    >
      {children}
      <Overlay
        show={loading}
        style={{ background: "rgba(255, 255, 255, 0.3)" }}
      >
        <View
          style={{
            height: "100vh",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <Loading />
        </View>
      </Overlay>
    </ConfigProvider>
  );
};
export default Layout;
