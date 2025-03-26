import React from "react";
import OrderList from "../../OrderList";
import { useRouter } from "@tarojs/taro";

const Index: React.FC = () => {
  const router = useRouter();
  const groupId = router.params?.groupId as string;

  return groupId ? (
    <OrderList key={groupId} groupId={groupId} pageSize={400} />
  ) : null;
};
export default Index;
