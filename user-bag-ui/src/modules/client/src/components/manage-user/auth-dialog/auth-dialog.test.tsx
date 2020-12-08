import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import AuthDialog from "./index";
import '@testing-library/jest-dom/extend-expect';

describe('Auth Dialog', () => {
    const onSubmit = jest.fn();
    test('submit button is disabled when fields not populated and enables when populated', () => {
        const { getByTestId, getByLabelText } = render(<AuthDialog onSubmit={onSubmit} />);

        const button = getByTestId('auth-submit-button');
        expect(button).toBeDisabled();

        const idInput = getByLabelText('ID');
        const nameInput = getByLabelText('Password');

        fireEvent.change(idInput, setInputValue('1'));
        fireEvent.change(nameInput, setInputValue('Arthur Morgan'));

        expect(button).toBeEnabled();
    });

    test('onSubmit is executed when user clicks submit button', () => {
        const { getByTestId, getByLabelText } = render(<AuthDialog onSubmit={onSubmit} />);

        const button = getByTestId('auth-submit-button');
        const idInput = getByLabelText('ID');
        const nameInput = getByLabelText('Password');

        fireEvent.change(idInput, setInputValue('1'));
        fireEvent.change(nameInput, setInputValue('Arthur Morgan'));
        fireEvent.click(button);

        expect(onSubmit).toHaveBeenCalledTimes(1);
    });
});

const setInputValue = (value: string) => {
    return {
        target: {
            value: value
        }
    };
}