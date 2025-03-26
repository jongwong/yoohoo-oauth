import { Popup, PopupProps } from "@antmjs/vantui";
import React from "react";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import omitBy from "lodash-es/omitBy";
import isNil from "lodash-es/isNil";

type StatisticsPopupProps = {
  groupId: string;
} & PopupProps;
const StatisticsPopup: React.FC<StatisticsPopupProps> = (props) => {
  const { show, groupId, ...rest } = props;

  const { data: orderData } = useRequest(
    () => {
      const val = {
        page: 1,
        size: 400,
        group_id: groupId,
      };
      return request.get("/client/admin/order/with_refund", {
        params: omitBy(val, isNil),
      });
    },
    {
      refreshDeps: [groupId, show],
      ready: !!groupId && !!show,
    }
  );
  console.log("=====orderData=====", orderData);

  return <Popup {...rest} show={show}></Popup>;
};
export default StatisticsPopup;
