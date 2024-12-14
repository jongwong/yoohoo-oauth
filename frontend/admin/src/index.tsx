import * as ReactDOMClient from 'react-dom/client';

import App from './App';

/* eslint-disable */
const rootElement = document.getElementById('root');
// @ts-ignore
const root = ReactDOMClient.createRoot(rootElement);

root.render(
    <App/>
);
