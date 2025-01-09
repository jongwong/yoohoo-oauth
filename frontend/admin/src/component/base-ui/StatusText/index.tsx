import React from 'react';
import { Badge, BadgeProps, theme } from 'antd';
import { get, isNil } from 'lodash';

const { useToken } = theme;
export type StatusTextProps = BadgeProps & {
	dot?: boolean;
};
const StatusText: React.FC<StatusTextProps> = props => {
	const { token } = useToken();

	const formatColor = () => {
		if (props?.color) {
			return get(token, props?.color);
		}
		if (props?.status) {
			//["success", "processing", "error", "default", "warning"]
			const map = {
				warning: 'colorWarning',
				success: 'colorSuccess',
				error: 'colorError',
				default: 'colorTextSecondary', // 使用次要文本颜色
				processing: 'colorPrimary', // 使用主题主色
			};
			return get(token, map[props.status]);
		}
	};
	if (props?.dot || isNil(props?.dot)) {
		return (
			<Badge
				status={props?.status as any}
				color={props?.color}
				text={<span style={{ color: formatColor() }}>{props?.text}</span>}
			/>
		);
	}
	return <span style={{ color: formatColor() }}>{props?.text}</span>;
};
export default StatusText;
