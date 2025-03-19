import React, { ReactNode } from "react";

import { ConfigProvider, Loading, Overlay } from "@antmjs/vantui";

import classNames from "classnames";

import styles from "./index.module.less";
import { View } from "@tarojs/components";

type LayoutProps = {
  children?: React.ReactNode;
  style?: React.CSSProperties;
  edge?: "none" | false;
  loading?: boolean;
  backgroundColor?: string;
  footer?: ReactNode;
};
const Layout: React.FC<LayoutProps> = (props) => {
  const { children, footer, backgroundColor, loading, edge, style, ...rest } =
    props;

  let _edge = edge;
  if (edge === false) {
    _edge = "none";
  }
  console.log("=====_edge=====", _edge);
  return (
    <ConfigProvider className={styles["yo-layout-wrapper"]}>
      <View
        className={classNames(
          styles["yoohoo-layout"],
          edge && styles["yoohoo-layout-edge-" + _edge],
          !!footer && styles["yo-layout-has-footer"]
        )}
        style={{ backgroundColor, ...style }}
      >
        {children}
      </View>
      {footer ? (
        <View className={styles["yo-layout-footer"]}>{footer}</View>
      ) : null}
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
