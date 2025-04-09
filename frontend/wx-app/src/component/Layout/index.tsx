import React, { ReactNode } from "react";

import {
  ConfigProvider,
  Loading,
  Overlay,
  Sticky,
  Tab,
  Tabs,
} from "@antmjs/vantui";

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
  header?: ReactNode;
  tabList?: {
    key: string;
    label: ReactNode;
    children: ReactNode;
  }[];
};
const Layout: React.FC<LayoutProps> = (props) => {
  const {
    children,
    footer,
    header,
    backgroundColor,
    loading,
    edge,
    style,
    tabList,
    ...rest
  } = props;

  let _edge = edge;
  if (edge === false) {
    _edge = "none";
  }
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
        {header ? <Sticky>{header}</Sticky> : null}
        {!tabList?.length ? (
          <View className={styles["yo-layout-content"]}>{children}</View>
        ) : (
          <Tabs>
            {tabList?.map((it) => (
              <Tab {...it} title={it.label} key={it.key} />
            ))}
          </Tabs>
        )}

        {footer ? (
          <View className={styles["yo-layout-footer"]}>{footer}</View>
        ) : null}
      </View>

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
