import Layout from "@/component/Layout";
import React, { useState } from "react";

import AdminGroupList from "@/pages/admin/group/list";
import { Tab, Tabs } from "@antmjs/vantui";
import styles from "@/pages/order/index.module.less";
import { View } from "@tarojs/components";
import OrderList from "@/pages/admin/OrderList";

enum ETabType {
  GROUP = 10,
  ORDER = 20,
}

const tabList = [
  {
    value: 10,
    title: "团购",
  },
  {
    value: 20,
    title: "退款",
  },
];
const AdminPage: React.FC = () => {
  const [currentStatus, setCurrentStatus] = useState(ETabType.GROUP);
  return (
    <Layout edge={"none"} footer={<View></View>}>
      <Tabs
        className={styles.tabs}
        active={currentStatus}
        onChange={(e) => {
          const find = tabList.find((_it, idx) => idx === e.detail.index);

          setCurrentStatus(find?.value);
        }}
      >
        {tabList.map((it) => (
          <Tab key={it.value} title={it.title}></Tab>
        ))}
      </Tabs>
      {currentStatus === ETabType.GROUP ? <AdminGroupList /> : null}
      {currentStatus === ETabType.ORDER ? <OrderList /> : null}
    </Layout>
  );
};
export default AdminPage;
