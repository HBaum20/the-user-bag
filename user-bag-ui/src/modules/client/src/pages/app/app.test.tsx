import React from 'react';
import {render, fireEvent, screen, act, waitFor} from '@testing-library/react';
import App from "./index";
import '@testing-library/jest-dom/extend-expect';
import axios from 'axios';
import MockAdapter from 'axios-mock-adapter';
import {UserModel} from "../../../../../models";

describe('Main Application Page', () => {

    const userData: UserModel[] = [
        {
            id: '1',
            name: 'Arthur Morgan',
            balance: 8000.0,
            email: 'arthur.morgan@skybettingandgaming.com',
            phone: '07714282896',
            password: 'password'
        },
        {
            id: '2',
            name: 'Charles Smith',
            balance: 12000,
            email: 'charles.smith@skybettingandgaming.com',
            phone: '07714282896',
            password: 'password'
        }
    ];

    const singleUser: UserModel = {
        id: '1',
        name: 'Arthur Morgan',
        balance: 8000.0,
        email: 'arthur.morgan@skybettingandgaming.com',
        phone: '07714282896',
        password: 'password'
    };

    beforeEach(() => {
        jest.clearAllMocks();
    });
    test('Should render image by default' ,() => {
        const { getByRole } = render(<App />);

        expect(getByRole('img')).toBeInTheDocument();
    });
    test('Should render the header component', () => {
        const { getByText } = render(<App />);

        expect(getByText('The User Bag')).toBeInTheDocument();
        expect(getByText('A practice project to store, process and retrieve users')).toBeInTheDocument();
    });
    test('Should render create user form when create user button is clicked', () => {
        const { queryByLabelText, getByTestId } = render(<App />);
        const labels = ['ID', 'Name', 'Initial Deposit', 'Email Address', 'Phone Number', 'Password', 'Confirm Password'];

        let createUserInputFields = getAllFields(labels, queryByLabelText);

        createUserInputFields.forEach(field => expect(field).toBeNull());

        fireEvent.click(getByTestId('app-create-user'));
        createUserInputFields = getAllFields(labels, queryByLabelText);
        createUserInputFields.forEach(field => expect(field).toBeInTheDocument());
    });
    test('Should render all users when all users button is clicked', async () => {
        const { getByTestId, getAllByTestId, queryByText } = render(<App />);

        const moxios = new MockAdapter(axios);
        moxios.onGet('/users').reply(200, userData);

        expect(queryByText('Name: Arthur Morgan')).toBeNull();
        expect(queryByText('Name: Charles Smith')).toBeNull();

        await act(async () => {
            fireEvent.click(getByTestId('app-all-users'));
        });

        await waitFor(() => getAllByTestId('user-view'));

        expect(queryByText('Name: Arthur Morgan')).toBeInTheDocument();
        expect(queryByText('Name: Charles Smith')).toBeInTheDocument();
    });
    test('Should render auth dialog when manage user button is clicked', () => {
        const { queryByLabelText, getByTestId } = render(<App />);

        expect(queryByLabelText('ID')).toBeNull();
        expect(queryByLabelText('Password')).toBeNull();

        fireEvent.click(getByTestId('app-manage-user'));

        expect(queryByLabelText('ID')).toBeInTheDocument();
        expect(queryByLabelText('Password')).toBeInTheDocument();
    });
    test('Should render manage user view when manage user button clicked and credentials entered', async () => {
        const { getByLabelText, getByTestId } = render(<App />);

        const moxios = new MockAdapter(axios);
        moxios.onGet('/users/1?password=password').reply(200, singleUser);

        fireEvent.click(getByTestId('app-manage-user'));

        fireEvent.change(getByLabelText('ID'), { target: { value: '1'} });
        fireEvent.change(getByLabelText('Password'), { target: { value: 'password'} });

        fireEvent.click(getByTestId('auth-submit-button'));

        await waitFor(() => getByLabelText('Name'));
        const labels = [
            'ID',
            'Name',
            'Balance',
            'Email Address',
            'Phone Number',
            'Hashed Password'
        ];

        getAllFields(labels, getByLabelText).forEach(field => expect(field).toBeInTheDocument());
    });

});

const getAllFields = (labels: string[], getter: Function) =>  labels.map(label => getter(label));