import React, { useEffect, useState } from 'react';
import http from '@/utils/http';

type HomeProps = {};
const Home: React.FC<HomeProps> = props => {
    const {...rest} = props;

    const [testState, setTestState] = useState("");
    const testRequest = async () => {

        const response = await http.get('/admin/user');
        setTestState(response.data)
        return response.data; // 假设返回值中包含 token
    };

    useEffect(() => {
        testRequest()
    }, []);

    return <div>Home</div>;
};
export default Home;
