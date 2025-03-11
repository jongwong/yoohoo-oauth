import Taro from "@tarojs/taro";

type StatusType = "success" | "error" | "info" | "warning";

interface StatusPageParams {
  status: StatusType;
  title?: string;
  buttons?: {
    text: string;
    actionType?: "navigateTo" | "redirectTo" | "switchTab" | "reLaunch";
    url: string;
  }[];
}

export function navigateToStatusPage(params: StatusPageParams) {
  const query = encodeURIComponent(JSON.stringify(params)); // 转换成字符串并编码
  Taro.navigateTo({
    url: `/pages/status-page/index?data=${query}`,
  });
}
