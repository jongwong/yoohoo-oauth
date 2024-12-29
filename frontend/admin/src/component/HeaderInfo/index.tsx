import React, { Key, ReactNode } from 'react';

import { Descriptions, Space, Statistic } from 'antd';
import { get } from 'lodash';

import { BaseFormProFieldType } from '@/component/pro-component/types';
import useFormatFields from '@/component/pro-component/useFormatFields';

type ItemType = {
	label: ReactNode;
	key?: Key;
	name?: Key | Key[];
	render?: (t: any, r: any) => ReactNode;
} & Pick<BaseFormProFieldType, 'valueType' | 'valueEnum'>;
export type HeaderInfoProps = {
	data?: any;
	leftItems?: ItemType[];
	rightItems?: ItemType[];
	extra?: ReactNode;
};
const HeaderInfo: React.FC<HeaderInfoProps> = props => {
	const { data, extra, leftItems = [], rightItems = [] } = props;

	const { formatField } = useFormatFields();
	return (
		<div
			style={{
				display: 'flex',
				justifyContent: 'space-between', // 左右内容分散
				alignItems: 'flex-start', // 保证内容顶部对齐
				position: 'relative', // 父容器相对定位
				width: '100%',
			}}>
			{/* 左侧基础信息 */}
			<Descriptions
				size={'small'}
				className={'mb-8'}
				style={{
					flex: '1 1 60%', // 左侧占 45% 宽度
					minWidth: '300px', // 确保最小宽度
				}}
				column={{
					xxl: 3,
					xl: 3,
					lg: 2,
					md: 2,
					sm: 1,
					xs: 1,
				}}>
				{leftItems?.map(it => {
					const _it = formatField?.(it);

					return (
						<Descriptions.Item key={_it.name || (_it.key as any)} label={_it?.label}>
							{_it?.render?.(get(data, (_it as any)?.name), data)}
						</Descriptions.Item>
					);
				})}
			</Descriptions>

			{/* 右侧状态信息 */}
			<div
				style={{
					flex: '1 1 40%', // 右侧占 45% 宽度
					minWidth: '300px', // 确保最小宽度
					display: 'flex',
					justifyContent: 'flex-end', // 右对齐
					alignItems: 'flex-start',
				}}>
				<Space>
					{rightItems?.map(it => {
						const _it = formatField?.(it);
						return (
							<Statistic
								key={_it.name || (_it.key as any)}
								title={_it?.label}
								valueStyle={{ fontSize: '18px', fontWeight: 'unset' }}
								value={get(data, (_it as any)?.name)}
								formatter={value => _it?.render?.(value, data)}
							/>
						);
					})}
				</Space>

				{/* 额外的内容（按钮等） */}
				<Space className={'ml-24'}>{extra}</Space>
			</div>
		</div>
	);
};
export default HeaderInfo;
