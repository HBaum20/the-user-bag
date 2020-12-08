import React from 'react';
import { render, fireEvent, screen } from '@testing-library/react';
import CreateUserForm from "./index";
import '@testing-library/jest-dom/extend-expect';
import axios from 'axios';

describe('Create User Form', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    })

    const onBack = jest.fn();
    const onSuccess = jest.fn();

    const getAllInputFields = (getter: Function) => {
        return {
            idField: getter('ID'),
            nameField: getter('Name'),
            depositField: getter('Initial Deposit'),
            emailField: getter('Email Address'),
            phoneField: getter('Phone Number'),
            passwordField: getter('Password'),
            confirmPasswordField: getter('Confirm Password')
        };
    }

    test('Should render all input fields', () => {
        const { getByLabelText } = render(<CreateUserForm onBack={onBack} onSuccess={onSuccess} />);
        const {
            idField,
            nameField,
            depositField,
            emailField,
            phoneField,
            passwordField,
            confirmPasswordField
        } = getAllInputFields(getByLabelText);

        expect(idField).toBeInTheDocument();
        expect(nameField).toBeInTheDocument();
        expect(depositField).toBeInTheDocument();
        expect(emailField).toBeInTheDocument();
        expect(phoneField).toBeInTheDocument();
        expect(passwordField).toBeInTheDocument();
        expect(confirmPasswordField).toBeInTheDocument();
    });

    test('Submit button should be disabled when fields are not populated', () => {
        const { getByLabelText, getByTestId } = render(<CreateUserForm onBack={onBack} onSuccess={onSuccess} />);
        const {
            idField,
            nameField,
            depositField,
            emailField,
            phoneField,
            passwordField,
            confirmPasswordField
        } = getAllInputFields(getByLabelText);
        const submitButton = getByTestId('create-user-submit-button');
        expect(submitButton).toBeDisabled();
        fireEvent.change(idField, { target: { value: '1' } })
        fireEvent.change(nameField, { target: { value: 'Arthur Morgan' } });
        fireEvent.change(depositField, { target: { value: 8000 } });
        fireEvent.change(emailField, { target: { value: 'arthur.morgan@skybettingandgaming.com' } });
        fireEvent.change(phoneField, { target: { value: '07714 282896' } });
        fireEvent.change(passwordField, { target: { value: 'password' } });
        fireEvent.change(confirmPasswordField, { target: { value: 'password' } });
        expect(submitButton).toBeEnabled();
    });

    test('throws error when password and confirm password don\'t match', async () => {
        const { getByLabelText, getByTestId, findByText } = render(<CreateUserForm onBack={onBack} onSuccess={onSuccess} />);
        const {
            idField,
            nameField,
            depositField,
            emailField,
            phoneField,
            passwordField,
            confirmPasswordField
        } = getAllInputFields(getByLabelText);
        const submitButton = getByTestId('create-user-submit-button');

        axios.get = jest.fn();

        fireEvent.change(idField, { target: { value: '1' } });
        fireEvent.change(nameField, { target: { value: 'Arthur Morgan' } });
        fireEvent.change(depositField, { target: { value: 8000 } });
        fireEvent.change(emailField, { target: { value: 'arthur.morgan@skybettingandgaming.com' } });
        fireEvent.change(phoneField, { target: { value: '07714 282896' } });
        fireEvent.change(passwordField, { target: { value: 'password' } });
        fireEvent.change(confirmPasswordField, { target: { value: 'not password' } });

        fireEvent.click(submitButton);
        const errMsg = await findByText('password and confirm password don\'t match');
        expect(errMsg).toBeInTheDocument();
        expect(axios.get).toBeCalledTimes(0);
    });
});