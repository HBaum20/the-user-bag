import React from 'react';
import axios from 'axios';
import { UserModel } from "../../../../../models";
import '@testing-library/jest-dom/extend-expect';
import { render, fireEvent} from "@testing-library/react";
import AllUsers from "./index";
import MockAdapter from "axios-mock-adapter";

describe('All Users Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });
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
    test('should retrieve users from API and render to page', async () => {
        let moxios = new MockAdapter(axios);

        moxios.onGet('/users').reply(200, userData);

        const onBackHome = jest.fn();

        const {findAllByTestId} = render(<AllUsers onBackHome={onBackHome} />);

        const users = await findAllByTestId('user-view')
        expect(users).toHaveLength(2);
        const user1Info = users[0].childNodes;
        expect(user1Info[0]).toContainHTML('ID: 1');
        expect(user1Info[1]).toContainHTML('Name: Arthur Morgan');
        const user2Info = users[1].childNodes;
        expect(user2Info[0]).toContainHTML('ID: 2');
        expect(user2Info[1]).toContainHTML('Name: Charles Smith');
    });
    test('should execute onBackHome() when back button is clicked', async () => {
        let moxios = new MockAdapter(axios);

        moxios.onGet('/users').reply(200, userData);

        const onBackHome = jest.fn();

        const { getByTestId } = render(<AllUsers onBackHome={onBackHome} />);

        fireEvent.click(getByTestId('all-users-back-button'));
        expect(onBackHome).toHaveBeenCalledTimes(1);
    });
});