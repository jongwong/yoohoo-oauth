import React from 'react';
import OssUpload from "@/component/OssUpload";
import {Card} from "antd";

type HomeProps = {};
const Home: React.FC<HomeProps> = props => {
    const {...rest} = props;

    return <Card> <OssUpload/></Card>;
};
export default Home;
