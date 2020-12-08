import React from 'react';
import Header from "./index";
import {render, screen} from '@testing-library/react';
import '@testing-library/jest-dom/extend-expect';

describe('Header', () => {
    test('renders title and subtitle', () => {
        const title = "TITLE";
        const subtitle = "SUBTITLE";

        render(<Header title={title} subtitle={subtitle} />);

        expect(screen.getByText(title)).toBeInTheDocument();
        expect(screen.getByText(subtitle)).toBeInTheDocument();
    });
});