import React, {useState} from 'react';
import Header from '../../components/header';
import UserOptions from '../../components/user-options';
import CreateUserForm from '../../components/create-user-form';

import '@blueprintjs/core/lib/css/blueprint.css'
import {UserModel} from "../../../../../models";
import axios from 'axios';
import AllUsers from "../../components/all-users";
import AuthDialog from "../../components/manage-user/auth-dialog";
import AppToaster from "../../tools/toaster";
import ManageUser from "../../components/manage-user";

enum ActiveComponent {
    IMAGE,
    CREATE_USER,
    MANAGE_USER,
    ALL_USERS,
    AUTH_DIALOG
}

const App: React.FC = () => {

    const [activeComponent, setActiveComponent] = useState(ActiveComponent.IMAGE)
    const [user, setUser] = useState<UserModel | null>(null);

    const onManageUsersSubmit = (id: string, password: string): void => {
        axios.get(`/users/${id}?password=${password}`)
            .then(response => {
                setUser(response.data);
                setActiveComponent(ActiveComponent.MANAGE_USER);
            })
            .catch(err => {
                AppToaster.show({message: err.toString(), intent: 'danger'})
            });
    }

    const renderMiddlePart = () => {
        switch(activeComponent) {
            case ActiveComponent.IMAGE:
                return (
                    <img
                        src="https://www.nps.gov/lacl/learn/nature/images/LACL_2013_BullMooseTelaquana_JMills_1.jpg?maxwidth=1200&maxheight=1200&autorotate=false"
                        alt="pic"
                        height="70%"
                        width="100%"/>)
            case ActiveComponent.CREATE_USER:
                return (
                    <CreateUserForm
                        onBack={() => setActiveComponent(ActiveComponent.IMAGE)}
                        onSuccess={() => setActiveComponent(ActiveComponent.IMAGE)}
                    />
                )
            case ActiveComponent.ALL_USERS:
                return <AllUsers onBackHome={() => setActiveComponent(ActiveComponent.IMAGE)}/>
            case ActiveComponent.AUTH_DIALOG:
                return <AuthDialog onSubmit={onManageUsersSubmit}/>
            case ActiveComponent.MANAGE_USER:
                if(user != null) {
                    return <ManageUser user={user} onBack={() => setActiveComponent(ActiveComponent.IMAGE)} />
                }
                return null;
            default:
                return null;    
        }
    }
    return (
        <div>
            <Header title={"The User Bag"}
                    subtitle={"A practice project to store, process and retrieve users"} />
            {renderMiddlePart()}
            <UserOptions
                onCreateUser={() => setActiveComponent(ActiveComponent.CREATE_USER)}
                onAllUsers={() => setActiveComponent(ActiveComponent.ALL_USERS)}
                onManageUser={() => setActiveComponent(ActiveComponent.AUTH_DIALOG)}
            />
        </div>
    );
};


export default App;