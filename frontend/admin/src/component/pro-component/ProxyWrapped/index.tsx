import type React from 'react';
import { cloneElement, isValidElement } from 'react';

import { isFunction, omit } from 'lodash';

type ReturnType = React.ReactElement;

export type ProxyWrappedProps<T = any> = {
  value?: any;
  onChange?: (args: any[]) => any;
  children?: React.ReactNode | ((config: { value: any; onChange?: (e: any) => any; [key: string]: any }) => ReturnType);
  render?: (props: { value?: any; onChange?: (args: any[]) => any; [key: string]: any }) => ReturnType;
};

const ProxyWrapped: <T = any>(props: ProxyWrappedProps<T>) => ReturnType = props => {
  const { render, value = undefined, onChange = () => null, ...rest } = props;
  const realProps = omit(rest, ['render']);
  if (typeof render === 'function') {
    return render({ value, onChange, ...realProps }) as ReturnType;
  }
  const newProps: any = { ...rest };

  if (isFunction(props?.children)) {
    return props?.children(omit({ value, onChange, ...newProps }, ['children']) as any) as ReturnType;
  }

  const el = props?.children && isValidElement(props.children) ? cloneElement(props.children, { value, onChange, ...newProps }) : props.children;
  return el as ReturnType;
};
export default ProxyWrapped;
