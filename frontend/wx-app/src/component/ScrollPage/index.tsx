import React, { ReactNode, useImperativeHandle, useRef, useState } from "react";
import {
  InfiniteScroll,
  InfiniteScrollProps,
  IPullToRefreshProps,
  PullToRefresh,
} from "@antmjs/vantui";

interface UseInfiniteScrollProps<T> {
  request: (params: {
    page: number;
    size: number;
  }) => Promise<{ success: boolean; data: T[]; total?: number }>;
  pageSize?: number;
  ready?: boolean;
}

function index<T>({ request, pageSize = 20 }: UseInfiniteScrollProps<T>) {
  const [data, setData] = useState<T[]>([]);
  const [pageNum, setPageNum] = useState(1);
  const InfiniteScrollInstance = useRef<any>();

  // 加载更多
  const loadMore: InfiniteScrollProps["loadMore"] = async () => {
    const res = await request({
      page: pageNum,
      size: pageSize,
    });
    if (!res.success) {
      return "error";
    }
    const newData = res.data || [];
    if (newData.length) {
      setData((prevData) => [...prevData, ...newData]);
      setPageNum((prev) => prev + 1);
    }
    return data.length + newData.length >= (res.total ?? 0)
      ? "complete"
      : "loading";
  };

  // 下拉刷新
  const onRefresh: IPullToRefreshProps["onRefresh"] = () => {
    return new Promise(async (resolve) => {
      const res = await request({
        page: 1,
        size: pageSize,
      });
      setPageNum(1);
      setData(res.data || []);
      if (data.length > 8) InfiniteScrollInstance.current?.reset();
      resolve(undefined);
    });
  };

  return {
    data,
    loadMore,
    onRefresh,
    InfiniteScrollInstance,
  };
}

const ScrollPage: React.FC<
  UseInfiniteScrollProps<any> & {
    children: (data: any[]) => ReactNode;
    actionRef?: any;
    ready?: boolean;
  }
> = ({ request, pageSize, ready, children, actionRef }) => {
  const {
    data: orderDataList,
    loadMore,
    onRefresh,
    InfiniteScrollInstance,
  } = index({
    request,
    pageSize,
  });
  useImperativeHandle(actionRef, () => ({
    reload: onRefresh,
  }));

  return (
    <PullToRefresh onRefresh={onRefresh} key={ready}>
      {children(orderDataList)}
      <InfiniteScroll loadMore={loadMore} ref={InfiniteScrollInstance} />
    </PullToRefresh>
  );
};
export default ScrollPage;
