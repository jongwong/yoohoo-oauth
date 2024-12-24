import React, { useEffect, useState } from 'react';

const UserDetail: React.FC = props => {
	const [testState, setTestState] = useState('');
	const testRequest = async () => {};

	useEffect(() => {
		testRequest();
	}, []);

	return <div>detail</div>;
};
export default UserDetail;
