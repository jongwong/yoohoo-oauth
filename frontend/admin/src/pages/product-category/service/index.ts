import http from '@/utils/http';

export const deleteProductCategoryById = (id: string) => {
	return http.delete('/admin/product-category/' + id);
};

export const getProductCategoryAll = (params: Record<string, any>) => {
	return http.get(`/admin/product-category/all`, { params });
};

export const getProductCategoryById = (id: string) => {
	return http.get(`/admin/product-category/${id}`);
};

export const getProductCategoryPage = (params: Record<string, any>) => {
	return http.get('/admin/product-category', { params });
};

// 创建配送点
export const createProductCategory = async (data: any) => {
	return http.post('/admin/product-category', data);
};

// 更新配送点
export const updateProductCategory = async (id: string, data: any) => {
	return http.put(`/admin/product-category/${id}`, data);
};

// 更新配送点
export const updateProductCategoryEnable = async (id: string) => {
	return http.put(`/admin/product-category/${id}/enable`);
};

// 更新配送点
export const updateProductCategoryDisable = async (id: string) => {
	return http.put(`/admin/product-category/${id}/disable`);
};
