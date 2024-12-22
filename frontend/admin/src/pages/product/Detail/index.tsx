import React, { useEffect } from 'react';
import http from '@/utils/http';
import { useParams } from 'react-router-dom';

const UserDetail: React.FC = props => {
	const params = useParams();
	const { productId } = params;
	const fetchDetailData = async () => {
		const response = await http.get('/admin/product/' + productId);

		return response.data; // 假设返回值中包含 token
	};

	useEffect(() => {
		fetchDetailData();
	}, []);

	return <div>detail</div>;
};
export default UserDetail;
