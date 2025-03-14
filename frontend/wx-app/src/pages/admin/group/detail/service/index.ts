// 创建团购
import request from "@/utils/request";

export const createPurchaseGroup = async (data: any) => {
  return request.post("/admin/purchase-group", data);
};

// 更新团购
export const updatePurchaseGroup = async (id: string, data: any) => {
  return request.put(`/admin/purchase-group/${id}`, data);
};

// 启用团购
export const updatePurchaseGroupEnable = async (id: string) => {
  return request.put(`/admin/purchase-group/${id}/enable`);
};

// 停用团购
export const updatePurchaseGroupDisable = async (id: string) => {
  return request.put(`/admin/purchase-group/${id}/disable`);
};
