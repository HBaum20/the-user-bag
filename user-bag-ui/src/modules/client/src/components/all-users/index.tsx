import React, {useEffect, useState} from 'react';
import {UserModel} from "../../../../../models";
import axios from 'axios';
import User from "./user";
import AppToaster from "../../tools/toaster";
import {Button} from "@blueprintjs/core";

interface AllUsersProps {
    onBackHome: () => void;
}

const AllUsers: React.FC<AllUsersProps> = ({onBackHome}) => {
    const [users, setUsers] = useState<UserModel[]>([]);

    useEffect(() => {
        axios.get('/users')
            .then(response => setUsers(response.data))
            .catch(reason => AppToaster.show({message: reason, intent: 'danger'}));
    }, []);

    return (
        <div>
            {users.map(user => <User id={user.id} name={user.name} key={user.id} />)}
            <Button onClick={onBackHome} large data-testid="all-users-back-button">Back</Button>
        </div>
    );
};
export default AllUsers;