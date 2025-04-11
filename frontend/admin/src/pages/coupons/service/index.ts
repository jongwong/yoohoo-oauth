import http from '@/utils/http';

export const deleteCouponsById = (couponsId: string) => {
	return http.delete('/admin/coupons/' + couponsId);
};

export const getCouponsById = (couponsId: string) => {
	return http.get('/admin/coupons/' + couponsId);
};

export const getCouponsPage = (params: Record<string, any>) => {
	return http.get('/admin/coupons', { params });
};

export const issueCouponsBatch = (id: string, data: { ids: string[] }) => {
	return http.post(`/admin/coupons/${id}/issue`, data);
};
