import React, { PureComponent, useEffect, useRef } from 'react';
import { WarningOutlined } from '@ant-design/icons';
import { has } from 'lodash';

class ErrorBound extends PureComponent {
  constructor(props: any) {
    super(props);
    this.state = {
      hasError: false,
    };
  }

  static getDerivedStateFromProps(props: any, state: any) {
    return {
      ...state,
      hasError: state?.hasError,
    };
  }

  static getDerivedStateFromError(error: any) {
    // 更新 state 使下一次渲染能够显示降级后的 UI
    return { hasError: true };
  }

  componentDidCatch(error: any, errorInfo: any) {
    (this.props as any)?.onDidCatch?.(error, errorInfo);
  }

  render() {
    (this.props as any)?.actionRef({
      clearError: () => {
        this.setState({
          hasError: undefined,
        });
      },
    });
    if ((this.state as any)?.hasError) {
      return has(this.props, 'fallbackRender') ? (this.props as any)?.fallbackRender?.(<DefaultErrorContent />) : <DefaultErrorContent />;
    }

    return (this.props as any)?.children;
  }
}
export const DefaultErrorContent = () => (
  <div className="text-yellow">
    出错啦 <WarningOutlined />
  </div>
);
export default (props: any) => {
  const actionRef = useRef<any>();
  useEffect(() => {
    // 属性变了清除错误状态
    actionRef.current?.clearError();
  }, [props]);
  return (
    <ErrorBound
      {...props}
      actionRef={(e: any) => {
        actionRef.current = e;
      }}
      onDidCatch={(err: any, info: any) => {
        props?.onDidCatch(err, info);
      }}
    />
  );
};
