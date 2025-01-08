import http from '@/utils/http';

export const deleteDistributionPointById = (id: string) => {
	return http.delete('/admin/distribution-point/' + id);
};

export const getDistributionPointById = (id: string) => {
	return http.get('/admin/distribution-point/' + id);
};

export const getDistributionPointPage = (params: Record<string, any>) => {
	return http.get('/admin/distribution-point', { params });
};

// 创建配送点
export const createDistributionPoint = async (data: any) => {
	return http.post('/admin/distribution-point', data);
};

// 更新配送点
export const updateDistributionPoint = async (id: string, data: any) => {
	return http.put(`/admin/distribution-point/${id}`, data);
};

// 更新配送点
export const updateDistributionPointEnable = async (id: string) => {
	return http.put(`/admin/distribution-point/${id}/enable`);
};

// 更新配送点
export const updateDistributionPointDisable = async (id: string) => {
	return http.put(`/admin/distribution-point/${id}/disable`);
};
