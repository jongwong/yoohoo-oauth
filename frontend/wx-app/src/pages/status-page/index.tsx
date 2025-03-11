import Taro from "@tarojs/taro";
import { View } from "@tarojs/components";
import { Button, Result } from "@antmjs/vantui";
import { useEffect, useState } from "react";

import styles from "./index.module.less";
import Layout from "@/component/Layout";

const StatusPage = () => {
  const [status, setStatus] = useState<"success" | "fail" | "info" | "warning">(
    "info"
  );
  const [title, setTitle] = useState("操作提示");
  const [desc, setDesc] = useState("执行了相关操作");
  const [buttons, setButtons] = useState<
    { text: string; action: () => void }[]
  >([]);

  useEffect(() => {
    const paramsStr = Taro.getCurrentInstance().router?.params?.data;
    if (paramsStr) {
      try {
        const params = JSON.parse(decodeURIComponent(paramsStr));
        setStatus(params.status || "info");
        setTitle(params.title || "操作提示");
        setDesc(params.desc || "执行了相关操作");

        if (Array.isArray(params.buttons)) {
          setButtons(
            params.buttons.map((btn) => ({
              text: btn.text,
              action: () =>
                Taro[btn.actionType || "navigateTo"]({ url: btn.url }),
            }))
          );
        }
      } catch (error) {
        console.error("解析参数失败", error);
      }
    }
  }, []);

  return (
    <Layout backgroundColor={"#fff"} style={{ height: "100vh" }}>
      <View className={styles["status-page"]}>
        <Result type={status} title={title} className={"w-1-1"} />
        <View className={styles["status-page-btns"]}>
          {buttons.map((btn, index) => (
            <Button key={index} block type="primary" onClick={btn.action}>
              {btn.text}
            </Button>
          ))}
        </View>
      </View>
    </Layout>
  );
};

export default StatusPage;
