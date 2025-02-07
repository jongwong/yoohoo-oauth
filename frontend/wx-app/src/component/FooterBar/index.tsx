import React from "react";

import styles from "./index.module.less";
import { View } from "@tarojs/components";

const FooterBar: React.FC<{
  children?: React.ReactNode;
}> = (props) => {
  return <View className={styles["yo-footer-bar"]}>{props?.children}</View>;
};
export default FooterBar;
