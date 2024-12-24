import React, { useMemo } from 'react';
import { Button, Col, Form, FormInstance, Row } from 'antd';
import { BaseFormProFieldType } from '@/component/pro-component/types';
import ProField from '@/component/pro-component/ProField';
import { formatRenderFun } from '@/component/pro-component/ProField/render/formatRenderUtil';
import { omit } from 'lodash';

export type QueryFormFieldType<T = any> = BaseFormProFieldType<T>;

export interface ProQueryFormProps<T = any> {
	fields: QueryFormFieldType<T>[];
	form?: FormInstance;
	onSearch?: (values: any) => void; // 查询按钮的回调
	onReset?: () => void; // 重置按钮的回调
	hideForm?: boolean;
}

const ProQueryForm: React.FC<ProQueryFormProps> = ({
	form: formProp,
	hideForm,
	fields,
	onSearch,
	onReset,
}) => {
	const [form] = Form.useForm(formProp);

	// 提交查询表单
	const handleSearch = () => {
		form.validateFields().then(values => {
			onSearch?.(values);
		});
	};

	// 重置表单
	const handleReset = () => {
		form.resetFields();
		onReset?.();
	};

	const formatProField: any = useMemo(() => {
		return fields?.map(it => ({
			...omit(it, ['title', 'dataIndex']),
			...formatRenderFun(it, {}),
		}));
	}, []);

	const renderContent = () => {
		return (
			<Row gutter={[16, 16]} style={{ width: '100%' }}>
				{formatProField.map((field: any) => (
					<Col key={field.name as string} xs={24} sm={12} md={8} lg={6}>
						<ProField plain {...field} mode={'edit'} />
					</Col>
				))}
				<Col style={{ textAlign: 'right', flex: 'auto' }}>
					<Button type="primary" onClick={handleSearch} style={{ marginRight: 8 }}>
						查询
					</Button>
					<Button onClick={handleReset}>重置</Button>
				</Col>
			</Row>
		);
	};

	if (hideForm) {
		return renderContent();
	}
	return (
		<Form form={form} layout="inline" style={{ marginBottom: 16 }}>
			{renderContent()}
		</Form>
	);
};

export default ProQueryForm;
