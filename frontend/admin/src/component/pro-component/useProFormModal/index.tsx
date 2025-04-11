import { Form, FormInstance, FormProps, Modal } from 'antd';
import type { ModalStaticFunctions } from 'antd/es/modal/confirm';
import React, { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import ProForm from '../ProForm';
import { ProFormItemsProps } from '@yoo/pro-component';
import type { ModalFuncProps } from 'antd/es/modal/interface';

type HookModalFuncProps = ModalFuncProps & {
	onOk?: (values: any, form: FormInstance, e: any) => Promise<any>;
	fields?: ProFormItemsProps['fields'];
	formProps?: FormProps;
};

type HookModalFuncWithPromise = (props: HookModalFuncProps) => {
	destroy: () => void;
	update: (configUpdate: Partial<HookModalFuncProps>) => void;
};

export type HookAPI = Record<keyof ModalStaticFunctions, HookModalFuncWithPromise>;

type ReturnType = {
	modal: HookModalFuncWithPromise;
} & HookAPI;

const useProFormModal: () => [ReturnType, JSX.Element] = () => {
	const [innerModal, Holder] = Modal.useModal();
	const [form] = Form.useForm();
	const [modalType, setModalType] = useState<string>();

	const [modalProps, setModalProps] = useState<any>({});

	const onOkHandle = async (onOk: any, e: any) => {
		await form.validateFields();
		const val = form.getFieldsValue(true);
		const _promise = onOk?.(val, form, e);

		if (_promise instanceof Promise) {
			_promise.then(() => {
				setModalProps({});
			});
		} else {
			setModalProps({});
		}
	};
	const getFunc = (name: string): any => {
		if (name !== 'modal') {
			const fn = (innerModal as any)?.[name];
			return (...args: any[]) => {
				setModalType(name);
				return fn?.(...args);
			};
		}

		return (cfg: any) => {
			setModalType(name);
			setModalProps({
				__uuid: uuidv4(),
				...cfg,
				open: true,
			});
			return {
				update: (mergeCfg: any) => {
					setModalProps((old: any) => ({
						...old,
						...mergeCfg,
					}));
				},
				destroy: () => {
					setModalProps({});
				},

				then: (() => {
					// 没有实现
					throw Error('then not implement');
				}) as any,
			};
		};
	};

	const renderContent = (cfg: any) => {
		if (!cfg?.open && modalType !== 'modal') {
			return null;
		}

		return (
			<Form form={form} {...cfg?.formProps}>
				{cfg?.fields?.length ? <ProForm.Items editable fields={cfg?.fields} /> : null}
				{cfg?.content}
			</Form>
		);
	};

	return [
		{
			modal: getFunc('modal'),
			info: getFunc('info'),
			success: getFunc('success'),
			error: getFunc('error'),
			warn: getFunc('warn'),
			warning: getFunc('warning'),
			confirm: getFunc('confirm'),
		} as ReturnType,
		<>
			{Holder}

			<Modal
				key={modalProps?.__uuid}
				{...modalProps}
				onOk={e => onOkHandle(modalProps?.onOk, e)}
				onCancel={() => {
					form.resetFields();
					setModalProps({});
				}}>
				{renderContent(modalProps)}
			</Modal>
		</>,
	];
};
export default useProFormModal;
