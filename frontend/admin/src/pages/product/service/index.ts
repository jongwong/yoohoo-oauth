import http from '@/utils/http';

export const deleteProductById = (id: string) => {
	return http.delete('/admin/product/' + id);
};

export const getProductById = (id: string) => {
	return http.get('/admin/product/' + id);
};

export const getProductPage = (params: Record<string, any>) => {
	return http.get('/admin/product', { params });
};
