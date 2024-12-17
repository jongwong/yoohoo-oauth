import React, { useEffect, useState } from 'react';
import http from '@/utils/http';

const UserDetail: React.FC = props => {
	const [testState, setTestState] = useState('');
	const testRequest = async () => {
		const response = await http.get('/admin/user');
		setTestState(response.data);
		return response.data; // 假设返回值中包含 token
	};

	useEffect(() => {
		testRequest();
	}, []);

	return <div>detail</div>;
};
export default UserDetail;
