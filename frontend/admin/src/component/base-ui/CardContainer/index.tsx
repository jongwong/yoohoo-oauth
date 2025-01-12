import React from 'react';
import { Space } from 'antd';

type CardContainerProps = {
	children?: React.ReactNode;
	size?: 'small' | 'middle' | 'large';
};
const CardContainer: React.FC<CardContainerProps> = props => {
	const { children, size = 'large', ...rest } = props;

	return (
		<Space direction={'vertical'} className={'w-1-1'} size={size}>
			{children}
		</Space>
	);
};
export default CardContainer;
