import React, { useMemo } from "react";
import useRequest from "@/hooks/useRequest";
import request from "@/utils/request";
import omitBy from "lodash-es/omitBy";
import isNil from "lodash-es/isNil";
import { View } from "@tarojs/components";
import Layout from "@/component/Layout";
import { useRouter } from "@tarojs/taro";
import Table from "@/component/Table";
import { Cell } from "@antmjs/vantui";
import { EOrderStatus } from "@/constant/order";

const Statistics: React.FC = () => {
  const router = useRouter();
  const groupId = router.params.groupId;
  const { data: orderData, loading: orderLoading } = useRequest(
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
      refreshDeps: [groupId],
      ready: !!groupId,
    }
  );

  const { data: groupDetailData, loading } = useRequest(
    () => {
      return request.get(`/client/admin/group/${groupId}`, {
        params: {},
      });
    },
    {
      refreshDeps: [groupId],
      ready: !!groupId,
    }
  );

  function groupOrdersBySKU(data = []) {
    const groupedData = {};

    data?.forEach((order) => {
      order?.items.forEach((item) => {
        const key = item.sku_name
          ? `${item.product_name} - ${item.sku_name}`
          : item.product_name;

        if (!groupedData[key]) {
          groupedData[key] = {
            title:
              item.product_name + (item.sku_name ? "  " + item.sku_name : ""),
            sku_name: item.sku_name || "",
            total_amount: 0,
            total_count: 0,
            orders: [],
          };
        }

        groupedData[key].total_amount += item.amount;
        groupedData[key].total_count += item.count;
        groupedData[key].orders.push(order);
      });
    });

    return Object.values(groupedData);
  }

  const getTitle = (item) => {
    return item.product_name + (item.sku_name ? "  " + item.sku_name : "");
  };

  function groupOrdersByMobile(data = []) {
    const groupedData = {};

    data?.forEach((order: any) => {
      order?.items.forEach((item) => {
        const key = item.consignee_mobile;

        if (!groupedData[key]) {
          groupedData[key] = {
            title: getTitle(item),
            sku_name: item.sku_name || "",
            total_amount: 0,
            total_count: 0,
            orders: [],
          };
        }

        const oldItemData = {
          ...item,
          ...order,
        };

        groupedData[key].consignee_mobile = order.consignee_mobile;
        groupedData[key].total_amount += item.amount;
        groupedData[key].total_count += item.count;
        groupedData[key].orders.push(oldItemData);
      });
    });

    return Object.values(groupedData);
  }

  const sortOrder = useMemo(() => {
    return (orderData || []).sort((a, b) => {
      return a.created_at - b.created_at;
    });
  }, [orderData]);
  const skuFormatData = useMemo(() => {
    return groupOrdersBySKU(sortOrder);
  }, [sortOrder]);
  const mobileFormatData = useMemo(() => {
    return groupOrdersByMobile(sortOrder);
  }, [sortOrder]);

  // 总数量
  const getTotalCount = (data) => {
    let totalCount = 0;
    data?.forEach((item) => {
      totalCount += item.total_count;
    });
    return totalCount;
  };

  const getRefundCount = () => {
    let totalCount = 0;
    orderData?.forEach((item) => {
      if (
        [
          EOrderStatus.PendingDelivery,
          EOrderStatus.Preparing,
          EOrderStatus.InDelivery,
          EOrderStatus.Completed,
        ].includes(item)
      ) {
        totalCount += item.total_count;
      }
    });
    return totalCount;
  };

  const renderInfo = () => {
    return (
      <View>
        <Cell title={"团购名称"} children={groupDetailData?.name}></Cell>
        <Cell
          title={"配送地址"}
          children={groupDetailData?.distribution_point_name}
        ></Cell>
        <Cell
          title={"数量统计"}
          children={
            <View>
              {getTotalCount(skuFormatData)}/ {getRefundCount()}
            </View>
          }
        ></Cell>
      </View>
    );
  };
  return (
    <Layout
      loading={orderLoading || loading}
      header={renderInfo()}
      tabList={[
        {
          key: "sku",
          label: "按SKU",
          children: (
            <>
              <Table
                dataSource={skuFormatData}
                columns={[
                  {
                    title: "序号",
                    dataIndex: "_index",
                    width: "40px",
                    render: (t, r, idx) => idx + 1,
                  },
                  {
                    title: "商品名称",
                    dataIndex: "title",
                    width: "250px",
                  },
                  {
                    title: "数量",
                    dataIndex: "total_count",
                    width: "40px",
                  },
                ]}
              />
            </>
          ),
        },
        {
          key: "mobile",
          label: "按收货人",
          children: (
            <Table
              dataSource={mobileFormatData}
              columns={[
                {
                  title: "商品",
                  dataIndex: "title",
                  width: "250px",
                  render: (t, r) => {
                    return (
                      <View>
                        {r?.orders?.map((item) => (
                          <View>
                            {getTitle(item)}
                            {" *"} {item?.count}
                          </View>
                        ))}
                      </View>
                    );
                  },
                },
                {
                  title: "数量",
                  dataIndex: "total_count",
                  width: "40px",
                },
                {
                  title: "手机",
                  dataIndex: "consignee_mobile",
                  width: "100px",
                },
              ]}
            />
          ),
        },
      ]}
    />
  );
};
export default Statistics;
