import http from '@/utils/http';

export const deletePurchaseGroupById = (id: string) => {
	return http.delete('/admin/purchase-group/' + id);
};

export const getPurchaseGroupProductPage = (params: Record<string, any>) => {
	return http.get(`/admin/purchase-group/product`, { params });
};

export const getPurchaseGroupAll = (params: Record<string, any>) => {
	return http.get(`/admin/purchase-group/all`, { params });
};

export const getPurchaseGroupById = (id: string) => {
	return http.get(`/admin/purchase-group/${id}`);
};

export const getPurchaseGroupPage = (params: Record<string, any>) => {
	return http.get('/admin/purchase-group', { params });
};

// 创建团购
export const createPurchaseGroup = async (data: any) => {
	return http.post('/admin/purchase-group', data);
};

// 更新团购
export const updatePurchaseGroup = async (id: string, data: any) => {
	return http.put(`/admin/purchase-group/${id}`, data);
};

// 启用团购
export const updatePurchaseGroupEnable = async (id: string) => {
	return http.put(`/admin/purchase-group/${id}/enable`);
};

// 停用团购
export const updatePurchaseGroupDisable = async (id: string) => {
	return http.put(`/admin/purchase-group/${id}/disable`);
};
